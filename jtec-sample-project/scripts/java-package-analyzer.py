import argparse
import os
import re
from pathlib import Path
from typing import Dict, Optional, Pattern, AnyStr


def parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "root_dir",
        help="Root directory where to start searching for Java files.",
    )
    parser.add_argument(
        "--excludes", "-e", required=False, help="Regex for excluded packages."
    )
    parser.add_argument(
        "--file-excludes", "-fe", required=False, help="Regex for excluding files."
    )
    parser.add_argument(
        "--output", "-o", required=False, help="Output file for packages."
    )
    return parser.parse_args()


def main():
    args = parse_arguments()
    root_dir: Path = Path(args.root_dir).resolve(strict=True)
    if not root_dir.exists() or not root_dir.is_dir():
        print(f"Invalid root directory {root_dir}")
        exit(1)
    regex: Optional[Pattern[AnyStr]] = None
    if args.excludes is not None:
        regex = re.compile(args.excludes, re.IGNORECASE)
    file_regex: Optional[Pattern[AnyStr]] = None
    if args.file_regex is not None:
        file_regex = re.compile(args.file_excludes, re.IGNORECASE)
    packages: Dict[str, str] = {}
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            filepath = Path(os.path.join(root, file))
            _, ext = os.path.splitext(file)
            if ext.lower() != ".java":
                continue
            if file_regex is not None and file_regex.match(filepath.__str__()):
                continue

            with filepath.open("r", encoding="latin-1") as fp:
                for line in fp.readlines():
                    match = re.search(r"package (\S*)\s*;", line)
                    if match is not None and len(match.groups()) > 0:
                        package = match.groups()[0]
                        if regex is None or not regex.match(package):
                            packages[package] = filepath.__str__()
                        break

    if args.output is not None:
        output: Path = Path(args.output).resolve()
        output.write_text("\n".join(sorted([f"{k}\t{v}" for k, v in packages.items()])))
    else:
        print(packages)


if __name__ == "__main__":
    main()
