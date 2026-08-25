# Copyright (c) Siemens AG, 2026
#
# SPDX-License-Identifier: MIT

"""Cross-process lock for IOT2050 firmware hardware operations."""

import contextlib
import fcntl
import os
import threading
from pathlib import Path


RESOURCE_LOCK_PATHS = {
    "system": "/run/iot2050/firmware-system.lock",
    "eio": "/run/iot2050/firmware-eio.lock",
}
_local = threading.local()


class FirmwareOperationBusy(RuntimeError):
    """Another firmware operation currently owns the hardware lock."""


@contextlib.contextmanager
def firmware_operation_lock(resource="system", blocking=False, path=None):
    if path is None and resource not in RESOURCE_LOCK_PATHS:
        raise ValueError(f"Unknown firmware resource: {resource}")
    lock_path = Path(path or RESOURCE_LOCK_PATHS[resource])
    depths = getattr(_local, "depths", {})
    depth = depths.get(str(lock_path), 0)
    if depth:
        depths[str(lock_path)] = depth + 1
        try:
            yield
        finally:
            depths[str(lock_path)] -= 1
        return

    lock_path.parent.mkdir(parents=True, exist_ok=True)
    descriptor = os.open(lock_path, os.O_CREAT | os.O_RDWR, 0o600)
    try:
        flags = fcntl.LOCK_EX
        if not blocking:
            flags |= fcntl.LOCK_NB
        try:
            fcntl.flock(descriptor, flags)
        except BlockingIOError as error:
            raise FirmwareOperationBusy("Another firmware operation is active") from error
        depths[str(lock_path)] = 1
        _local.depths = depths
        yield
    finally:
        depths.pop(str(lock_path), None)
        try:
            fcntl.flock(descriptor, fcntl.LOCK_UN)
        finally:
            os.close(descriptor)
