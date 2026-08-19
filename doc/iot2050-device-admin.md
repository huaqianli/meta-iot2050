# Device Admin Cockpit Plugin

The Example image includes the `iot2050-device-admin` Cockpit page under
**Device Admin**. It provides two device-local operations:

- install a customer HTTPS certificate and matching private key;
- download the image-bundled OSS Clearing ZIP without an Internet connection.

## HTTPS certificate installation

Upload a PEM certificate or full chain together with its matching unencrypted
PEM private key. The certificate already contains the public key; do not upload
a separate public-key file. The certificate should include the device hostname
or IP address used in the browser in its Subject Alternative Name (SAN).

Uploading the private key is required because nginx uses it to terminate HTTPS.
The key is not shown after upload. Use a certificate issued by a CA trusted by
the browser to avoid `Not secure` warnings. Installing a certificate reloads
nginx.

In a Product environment, protect the private key as a secret. Upload it only
to the intended device through a trusted management connection; do not expose
it in chat, logs, screenshots, or source control. If it may have been exposed,
replace the certificate and key with a newly generated pair.

The current self-signed certificate can still encrypt the connection, but the
browser cannot verify who issued it. For testing, use a controlled network and
verify that you are connected to the intended device before uploading a key.

## OSS Clearing package

The image contains exactly one current archive in this directory:

`/usr/share/iot2050/oss-clearing/`

The archive keeps the official versioned filename matching
`V<version>-ReadmeOSS-All_in_One.zip`. The recipe requires exactly one matching
archive, and the helper rejects a missing archive or an image containing more
than one matching archive.

The page reports the package as unavailable when the archive is missing or
when multiple matching archives are present.

## Runtime requirements

`iot2050-cockpit-device-admin` is installed by
[the Example image recipe](../meta-example/recipes-core/images/iot2050-image-example.bb).
It requires the nginx gateway, OpenSSL, Python 3, and systemd. The privileged
helper is `/usr/sbin/iot2050-device-admin`; it accepts only fixed operations
and fixed filesystem paths, so the page does not expose a general root shell
or arbitrary file download endpoint.
