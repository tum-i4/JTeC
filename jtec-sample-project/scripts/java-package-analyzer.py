import argparse
import os
import re
from pathlib import Path
from typing import Dict


def parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "root_dir",
        help="Root directory where to start searching for Java files.",
    )
    parser.add_argument("--output", "-o", required=False, help="Output file")
    return parser.parse_args()


def main():
    args = parse_arguments()
    root_dir: Path = Path(args.root_dir).resolve(strict=True)
    if not root_dir.exists() or not root_dir.is_dir():
        print(f"Invalid root directory {root_dir}")
        exit(1)
    packages: Dict[str, str] = {}
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            filepath = Path(os.path.join(root, file))
            _, ext = os.path.splitext(file)
            if ext.lower() != ".java":
                continue

            with filepath.open("r") as fp:
                for line in fp.readlines():
                    match = re.search(r"package (\S*)\s*;", line)
                    if len(match.groups()) > 0:
                        package = match.groups()[0]
                        packages[package] = filepath.__str__()
                        break

    if args.output is not None:
        output: Path = Path(args.output).resolve()
        output.write_text("\n".join(sorted([f"{k}\t{v}" for k, v in packages.items()])))
    else:
        print(packages)


if __name__ == "__main__":
    main()
