import io, os
from os import path
import re

for root, dirs, files in os.walk(r"c:\temp\eci\waylet pilot\zero price"):
        logs = [log for log in files if log.lower().endswith('txt') and log.lower().startswith('messages')]
        for log in logs:
            log_path = path.join(root, log)

            with io.open(log_path, "r", encoding="utf-8") as f:
                lines = f.readlines()

            for line in lines:
                if "ECIMPService.ProcessCapturedBackOfficeRecord Processing backoffice record. " in line:
                    print(re.findall(r" TranNumber: (\d+), FpId: (\d+), TransSeqNo: (\d+), Vol: (\d+), Money: (\d+)", line))
