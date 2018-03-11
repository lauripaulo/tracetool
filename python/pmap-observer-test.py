#!python3
"""Script to observe the output of the pmap command of a given
   pid and write its content into an output file each time it
   changes."""
import sys
import subprocess
from time import sleep
from time import time
from datetime import datetime


#           1         2         3         4         5         6         7         8
# 012345678901234567890123456789012345678901234567890123456789012345678901234567890
# lauri    14481  0.0  0.0  37404  3400 pts/2    R+   16:26   0:00 ps -aux
# 4130 ?        00:17:35 java
#    1 ?        00:00:00 systemd

# Test running instrumented with Valgrind.

def get_pid(pname):
    """Get the first pid from a process given its name
        """
    pid = None
    command = subprocess.run(['ps', '-e'], stdout=subprocess.PIPE)
    output = command.stdout.decode('ascii')
    lines = output.split('\n')
    for line in lines:
        if pname in line:
            columns = line.split()
            pid = columns[0]
            break
    return pid


def watch_pid(pid, outputfile):
    """Runs each second a pmap <pid> passed in argv[1] and compares
        it with the previous execution. If the content changes it writes
        the new content into the output file defined in argv[2]
        """
    stats_file = outputfile[:-4] + '-stats.csv'
    pmap_initial_file = outputfile[:-4] + '-initial.txt'
    pmap_final_file = outputfile[:-4] + '-final.txt'
    # Segment change counter
    segnum = 0
    # file names
    # init pmap stats file
    init_statsfile(stats_file)
    # watch for changes in the pmap command execution and write
    # the segments list when it happens.
    command = subprocess.run(['pmap', pid], stdout=subprocess.PIPE)
    output = command.stdout.decode('ascii')
    # write initial file
    write_file(pmap_initial_file, output)
    last_output = ''
    while len(output) > 0:
        if len(output) > 0:
            if len(output) != len(last_output):
                segnum += 1
                pmap_file = '{}-{}.txt'.format(outputfile[:-4], segnum)
                last_output = output
                write_file(pmap_file, last_output)
                process_libs(stats_file, last_output)
            else:
                print('No change...\n')
            sleep(0.01)
        last_output = command
        command = subprocess.run(['pmap', pid], stdout=subprocess.PIPE)
        output = command.stdout.decode('ascii')
    if len(last_output) > 0:
        write_file(pmap_final_file, last_output)
    print('Done!')


def init_statsfile(stats_file):
    # inits the stats csv file
    with open(stats_file, 'w') as myfile:
        myfile.write('timestamp; anon; libs; segments\n')


def write_file(outputfile, content):
    """Writes a pmap memory map file if it has any return from
        the command line.
        """
    if len(content) > 0:
        lines = content.split('\n')
        content = '\n'.join(lines[1:-2])
        print('\n')
        print(content)
        memmap_file = open(outputfile, 'w')
        memmap_file.write(content)
        memmap_file.close()


def process_libs(stats_file, content):
    '''
    count the number of library at a given execution time.    
    '''
    lines = content.split('\n')
    libcount = 0
    anoncount = 0
    ts = time()
    st = datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S.%f')
    for line in lines:
        if '.so' in line:
            libcount += 1
        if 'anon' in line:
            anoncount += 1
    # wite to csv stats file
    with open(stats_file, 'a') as myfile:
        myfile.write('"{}"; {}; {}; {}\n'.format(st, anoncount, libcount, len(lines)))
        print("Libs found: {}".format(libcount))
    return len(lines)

if __name__ == "__main__":
    print('Memory map observer (pmap) v0.5.1')
    if len(sys.argv) < 3:
        print('Usage: python3 pmap-observer.py <process name> <output file>\n')
    else:
        PID = get_pid(sys.argv[1])
        while PID is None:
            print('Process name not found or process id not running. Waiting...')
            PID = get_pid(sys.argv[1])
        else:
            OUTFILE = sys.argv[2]
            watch_pid(PID, OUTFILE)
