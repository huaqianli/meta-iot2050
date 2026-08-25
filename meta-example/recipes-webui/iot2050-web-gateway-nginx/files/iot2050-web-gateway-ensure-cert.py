#!/usr/bin/env python3
#
# Copyright (c) Siemens AG, 2026
#
# Authors:
#  Zhao Chun Jiao <chunjiao.zhao@siemens.com>
#
# SPDX-License-Identifier: MIT

"""Create the local HTTPS certificate used by the IOT2050 gateway."""

import datetime
import ipaddress
import os
import socket
import tempfile
from pathlib import Path

import psutil
from cryptography import x509
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.x509.oid import ExtendedKeyUsageOID, NameOID


CERT_DIR = Path("/etc/iot2050/web-gateway")
CERT_FILE = CERT_DIR / "tls.crt"
KEY_FILE = CERT_DIR / "tls.key"
SOURCE_FILE = CERT_DIR / "tls.source"


def local_addresses():
    addresses = {ipaddress.ip_address("127.0.0.1")}
    for interfaces in psutil.net_if_addrs().values():
        for address in interfaces:
            value = address.address.split("%", 1)[0]
            try:
                parsed = ipaddress.ip_address(value)
            except ValueError:
                continue
            if not parsed.is_unspecified:
                addresses.add(parsed)
    return sorted(addresses, key=lambda address: (address.version, str(address)))


def hostname():
    value = socket.getfqdn() or socket.gethostname()
    return value or "localhost"


def subject_alternative_names(name):
    names = [x509.DNSName("localhost")]
    if name != "localhost":
        names.append(x509.DNSName(name))
    names.extend(x509.IPAddress(address) for address in local_addresses())
    return x509.SubjectAlternativeName(names)


def write_atomic(path, data, mode):
    descriptor, temporary = tempfile.mkstemp(
        prefix=f".{path.name}.", dir=path.parent)
    try:
        os.fchmod(descriptor, mode)
        with os.fdopen(descriptor, "wb") as output:
            descriptor = None
            output.write(data)
            output.flush()
            os.fsync(output.fileno())
        os.replace(temporary, path)
    finally:
        if descriptor is not None:
            os.close(descriptor)
        try:
            os.unlink(temporary)
        except FileNotFoundError:
            pass


def create_certificate(name):
    key = rsa.generate_private_key(public_exponent=65537, key_size=4096)
    subject = issuer = x509.Name([
        x509.NameAttribute(NameOID.COMMON_NAME, name),
    ])
    now = datetime.datetime.utcnow()
    certificate = (
        x509.CertificateBuilder()
        .subject_name(subject)
        .issuer_name(issuer)
        .public_key(key.public_key())
        .serial_number(x509.random_serial_number())
        .not_valid_before(now - datetime.timedelta(minutes=1))
        .not_valid_after(now + datetime.timedelta(days=3650))
        .add_extension(subject_alternative_names(name), critical=False)
        .add_extension(x509.BasicConstraints(ca=False, path_length=None),
                       critical=True)
        .add_extension(
            x509.ExtendedKeyUsage([ExtendedKeyUsageOID.SERVER_AUTH]),
            critical=False,
        )
        .sign(key, hashes.SHA256())
    )
    return (
        key.private_bytes(
            serialization.Encoding.PEM,
            serialization.PrivateFormat.TraditionalOpenSSL,
            serialization.NoEncryption(),
        ),
        certificate.public_bytes(serialization.Encoding.PEM),
    )


def main():
    if all(
        path.is_file() and not path.is_symlink() and path.stat().st_size > 0
        for path in (CERT_FILE, KEY_FILE)
    ):
        return 0

    CERT_DIR.mkdir(mode=0o755, parents=True, exist_ok=True)
    CERT_DIR.chmod(0o755)
    try:
        SOURCE_FILE.unlink()
    except FileNotFoundError:
        pass

    key, certificate = create_certificate(hostname())
    write_atomic(KEY_FILE, key, 0o600)
    write_atomic(CERT_FILE, certificate, 0o644)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())