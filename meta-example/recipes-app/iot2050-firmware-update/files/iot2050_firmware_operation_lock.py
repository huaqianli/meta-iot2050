# Copyright (c) Siemens AG, 2026
#
# SPDX-License-Identifier: MIT

"""Cross-process lock for IOT2050 firmware hardware operations."""

import contextlib
import fcntl
import os
import threading
from pathlib import Path


LOCK_PATH = "/run/iot2050/firmware-operation.lock"
_local = threading.local()


class FirmwareOperationBusy(RuntimeError):
    """Another firmware operation currently owns the hardware lock."""


@contextlib.contextmanager
def firmware_operation_lock(path=None, blocking=False):
    depth = getattr(_local, "depth", 0)
    if depth:
        _local.depth = depth + 1
        try:
            yield
        finally:
            _local.depth -= 1
        return

    lock_path = Path(path or LOCK_PATH)
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
        _local.depth = 1
        yield
    finally:
        _local.depth = 0
        try:
            fcntl.flock(descriptor, fcntl.LOCK_UN)
        finally:
            os.close(descriptor)
