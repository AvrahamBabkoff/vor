import os
import re
import zipfile
import sys
import shutil

# print('number of args is ' + str(len(sys.argv)))
if (len(sys.argv) != 4):
    exit ()
root_dir = sys.argv[1]
delete_dir = sys.argv[2]
compress_dir = sys.argv[3]
# regular expression matching directory names with format 'YYYYMMYYHH'
prog_dir = re.compile('\d{4}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])(0[0-9]|1[0-9]|2[0-3])\Z')
# regular expression matching log file names 'log.MM'
prog_log_file = re.compile('log.[0-5][0-9]\Z')
# check that directory to delte matches regular expression
delete_result = prog_dir.match(delete_dir)
# check that directory to compress matches regular expression
compress_result = prog_dir.match(compress_dir)

# walk through all subdirectories
for subdir, dirs, files in os.walk(root_dir):
    d_path, d_name = os.path.split(subdir)
    s_result = prog_dir.match(d_name)
    if (delete_result and compress_result and s_result):
        if (d_name <= delete_dir):
            shutil.rmtree(subdir, ignore_errors=True, onerror=None)
        elif (d_name <= compress_dir):
            for file in files:
                filepath = subdir + os.sep + file
                f_path,f_name = os.path.split(file)
                s_result = prog_log_file.match(f_name)

                if s_result:
                    compressed_file_prefix = f_name[4:]
                    t_zip_file_full_path = os.path.join(subdir, compressed_file_prefix + '.zip')

                    with zipfile.ZipFile(t_zip_file_full_path, "w", zipfile.ZIP_DEFLATED) as zip_archive:
                        zip_archive.write(filepath, file)
                        zip_archive.close()
                        os.remove(filepath)
