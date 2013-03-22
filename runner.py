#!/usr/bin/env python

import datetime
import os
import select
import signal
import subprocess
import sys


class RunnerException(Exception):
    """Exception in Java code runner"""
    pass


class RunData(object):
    """Run statistics (return code, duration, etc.)"""

    def __init__(self, java_args, main_class, sys_args):
        """
        Create a RunData object

        java_args - arguments for the 'java' program
        main_class - fully-qualified name of class whose main() method
                    will be run
        sys_args - arguments for the class being run
        """
        self.__exitsig = False
        self.__killsig = False

        self.__returncode = None
        self.__run_time = None
        self.__wait_time = None

        self.__cmd = ["java", ]
        if java_args is not None:
            if type(java_args) == str:
                self.__cmd.append(java_args)
            elif type(java_args) == list or type(java_args) == tuple:
                self.__cmd += java_args
            else:
                raise RunnerException("Bad java_args type %s for %s" %
                                      (type(java_args), java_args))

        self.__cmd.append(main_class)
        if sys_args is not None:
            self.__cmd += sys_args

    @classmethod
    def __timediff(cls, start_time, end_time):
        """
        Convert the difference between two times to a floating point value
        """
        diff = end_time - start_time
        return float(diff.seconds) + \
            (float(diff.microseconds) / 1000000.0)

    def command(self):
        """Return the command which was run"""
        return self.__cmd

    def exit_signal(self):
        """Return the signal which caused the program to exit (or None)"""
        return self.__exitsig

    def kill_signal(self):
        """Return the signal which caused the program to be killed (or None)"""
        return self.__killsig

    def returncode(self):
        """Return the POSIX return code"""
        return self.__returncode

    def run_time(self):
        """Return the time needed to run the program"""
        return self.__run_time

    def set_exit_signal(self, val):
        """Record the signal which caused the program to exit"""
        self.__exitsig = val

    def set_kill_signal(self, val):
        """Record the signal which caused the program to be killed"""
        self.__killsig = val

    def set_return_code(self, val):
        """Record the POSIX return code"""
        self.__returncode = val

    def set_run_time(self, start_time, end_time):
        """Record the run time"""
        self.__run_time = self.__timediff(start_time, end_time)

    def set_wait_time(self, start_time, end_time):
        """Record the wait time"""
        self.__wait_time = self.__timediff(start_time, end_time)

    def wait_time(self):
        """Return the time spent waiting for the program to finish"""
        return self.__wait_time


class JavaRunner(object):
    """Wrapper which runs a Java program"""

    def __init__(self, main_class):
        """Create a JavaRunner instance"""
        self.__main_class = main_class
        self.__classes = []
        self.__classpath = None

        self.__proc = None
        self.__killsig = None
        self.__exitsig = None

    def __run_command(self, data, debug=False):
        """Run the Java program, tracking relevant run-related statistics"""
        self.__killsig = None
        self.__exitsig = None

        if debug:
            print " ".join(data.command())

        start_time = datetime.datetime.now()

        self.__proc = subprocess.Popen(data.command(), stdout=subprocess.PIPE,
                                       stderr=subprocess.PIPE,
                                       preexec_fn=os.setsid)
        num_err = 0
        while True:
            reads = [self.__proc.stdout.fileno(), self.__proc.stderr.fileno()]
            try:
                ret = select.select(reads, [], [])
            except select.error:
                # ignore a single interrupt
                if num_err > 0:
                    break
                num_err += 1
                continue

            for fd in ret[0]:
                if fd == self.__proc.stdout.fileno():
                    line = self.__proc.stdout.readline()
                    self.process(line, False)
                if fd == self.__proc.stderr.fileno():
                    line = self.__proc.stderr.readline()
                    self.process(line, True)

            if self.__proc.poll() is not None:
                break

        self.__proc.stdout.close()
        self.__proc.stderr.close()

        end_time = datetime.datetime.now()

        self.__proc.wait()

        wait_time = datetime.datetime.now()

        data.set_return_code(self.__proc.returncode)

        data.set_run_time(start_time, end_time)
        data.set_wait_time(end_time, wait_time)

        data.set_exit_signal(self.__exitsig)
        data.set_kill_signal(self.__killsig)

        self.__proc = None

    def add_subproject_jars(self, pkgs):
        """Add pDAQ jar files to CLASSPATH"""
        curdir = os.getcwd()
        parent = os.path.dirname(curdir)

        for pkg in pkgs:
            jar = None

            for d in (curdir, parent):
                tmpjar = os.path.join(d, "target",
                                      pkg + "-1.0.0-SNAPSHOT.jar")
                if os.path.exists(tmpjar):
                    jar = tmpjar
                    break

                tmpjar = os.path.join(d, pkg, "target",
                                      pkg + "-1.0.0-SNAPSHOT.jar")
                if os.path.exists(tmpjar):
                    jar = tmpjar
                    break

                tmpdir = os.path.join(d, pkg, "target", "classes")
                if os.path.exists(tmpdir):
                    jar = tmpdir
                    break

            if jar is None:
                raise RunnerException("Cannot find %s jar file" % pkg)

            self.__classes.append(jar)
            self.__classpath = None

    def add_repo_jars(self, rpkgs):
        """Add Maven repository jar files to CLASSPATH"""
        home = os.environ["HOME"]

        for (proj, name, version) in rpkgs:
            path = os.path.join(home, ".m2", "repository", proj, name, version,
                                "%s-%s.jar" % (name, version))
            if not os.path.exists(path):
                raise sys.exit(("Cannot find %s in local Maven repository" +
                                " (%s)") % (name, path))
            self.__classes.append(path)
            self.__classpath = None

    def kill(self, sig):
        self.send_signal(sig, None)
        self.__killsig = sig

    @classmethod
    def process(cls, line, is_stderr=False):
        """Process a line of output from the program"""
        if not is_stderr:
            sys.stdout.write(line)
            sys.stdout.flush()
        else:
            sys.stderr.write(line)

    def quickexit(self, sig, frame):
        """Kill the program if we get an interrupt signal"""
        self.send_signal(sig, frame)
        self.__exitsig = sig

    def run(self, sys_args=None, java_args=None, debug=False):
        """Run the Java program, handling ^C or ^\ as appropriate"""
        # set CLASSPATH if it hasn't been set yet
        if self.__classpath is None:
            self.__classpath = ":".join(self.__classes)
            os.environ["CLASSPATH"] = self.__classpath

            if debug:
                print "export CLASSPATH=\"%s\"" % os.environ["CLASSPATH"]

        signal.signal(signal.SIGINT, self.quickexit)
        signal.signal(signal.SIGQUIT, self.send_signal)

        try:
            rundata = RunData(java_args, self.__main_class, sys_args)
            self.__run_command(rundata, debug)
        finally:
            signal.signal(signal.SIGINT, signal.SIG_DFL)
            signal.signal(signal.SIGQUIT, signal.SIG_DFL)

        return rundata

    def send_signal(self, sig, frame):
        """Send a signal to the process"""
        if self.__proc is not None:
            os.killpg(self.__proc.pid, sig)
