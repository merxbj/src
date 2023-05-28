import os
import re
import shutil
from os import path

path = r'c:\VirtualBox VMs\Share\Repsol PT Import'

sem_template = r'<?xml version="1.0"?><FileList><File>{}</File></FileList>'


for root, dirs, files in os.walk(r'c:\VirtualBox VMs\Share\Repsol PT Import'):
    import_files = [import_file for import_file in files
                    if import_file.lower().endswith('xml')
                    and not import_file.lower().startswith('sem')]

    for import_file in import_files:
        sem_file_name = re.sub(r"^[a-zA-Z]{3,4}", "SEM", import_file)
        match = re.search(r"^([a-zA-Z]{3,4})", import_file)
        if match:
            prefix = match.group(1)
        else:
            prefix = "default"
        sem_file_name = re.sub(r"^[a-zA-Z]{3,4}", "SEM", import_file)
        target_folder = os.path.join(root, prefix)
        os.makedirs(target_folder, exist_ok=True)
        with open(os.path.join(target_folder, sem_file_name), mode="w", encoding="utf-8") as sem_file:
            sem_file.write(sem_template.format(import_file))
        shutil.move(os.path.join(root, import_file), os.path.join(target_folder, import_file))
