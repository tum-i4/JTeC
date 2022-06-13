# Sample Scripts

This directory contains sample scripts that are supposed to demonstrate the pre-test hook in practice.
Currently, we have the following examples:

- `frida-agent.py`: Simple [frida](https://frida.re/) agent that instruments all open system calls in the process (i.e.,
  test suite) it's gets attached to. Prerequisites: `pip install frida frida-tools`.

