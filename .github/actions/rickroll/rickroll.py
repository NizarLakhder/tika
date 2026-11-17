#!/usr/bin/env python3
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""IFT3913 task 3: rickroll the run when a test failed.

The JUnit XML reports of Surefire and Failsafe are read with regular expressions,
which is enough for their flat structure and needs no third-party XML library.
"""

import glob
import html
import os
import re

TESTCASE = re.compile(r"<testcase\b([^>]*?)(?:/>|>(.*?)</testcase>)", re.S)
ATTRIBUTE = re.compile(r'([\w-]+)="([^"]*)"')
# <failure> and <error> only: <flakyFailure> and <rerunFailure> are tests that passed on rerun
PROBLEM = re.compile(r"<(failure|error)\b([^>]*)")
MAX_ANNOTATIONS = 10  # GitHub shows at most 10 error annotations per step


def failing_tests(patterns):
    failures = []
    for pattern in patterns.split():
        for path in sorted(glob.glob(pattern, recursive=True)):
            with open(path, encoding="utf-8", errors="replace") as report:
                content = report.read()
            for attributes, body in TESTCASE.findall(content):
                problem = PROBLEM.search(body)
                if problem is None:
                    continue
                test = dict(ATTRIBUTE.findall(attributes))
                details = dict(ATTRIBUTE.findall(problem.group(2)))
                message = html.unescape(details.get("message", details.get("type", "")))
                failures.append({
                    "test": test.get("classname", "?") + "#" + test.get("name", "?"),
                    "kind": problem.group(1),
                    "message": " ".join(message.split()),
                })
    return failures


def escape_data(text):
    return text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")


def escape_property(text):
    return escape_data(text).replace(":", "%3A").replace(",", "%2C")


def summary(failures, video, thumbnail):
    lines = [
        "## You just got rickrolled",
        "",
        f"{len(failures)} test(s) failed, so here is a video while you fix them:",
        "",
        f"[![Rick Astley]({thumbnail})]({video})",
        "",
        "| Failing test | Type | Message |",
        "|---|---|---|",
    ]
    for failure in failures:
        # escaped so that "expected: <true>" is not read as an HTML tag
        message = html.escape(failure["message"][:200]).replace("|", "\\|")
        lines.append(f"| `{failure['test']}` | {failure['kind']} | {message} |")
    lines += ["", "Rick will leave as soon as the build is green again."]
    return "\n".join(lines) + "\n"


def append(variable, text):
    path = os.environ.get(variable)
    if path:
        with open(path, "a", encoding="utf-8") as target:
            target.write(text)
    else:
        print(text)


def main():
    video_id = os.environ.get("VIDEO_ID") or "dQw4w9WgXcQ"
    video = f"https://www.youtube.com/watch?v={video_id}"
    thumbnail = f"https://img.youtube.com/vi/{video_id}/hqdefault.jpg"
    failures = failing_tests(os.environ.get("REPORTS") or "**/TEST-*.xml")
    append("GITHUB_OUTPUT", f"failed-tests={len(failures)}\n")

    if not failures:
        print("The build failed, but no test did (compilation, checkstyle...): no rickroll.")
        return

    print("+--------------------------------------------+")
    print("|  A test failed. Incoming rickroll...       |")
    print("+--------------------------------------------+")
    for failure in failures:
        print(f"  {failure['kind']}: {failure['test']}  {failure['message'][:200]}")
    print(f"Your reward: {video}")

    for failure in failures[:MAX_ANNOTATIONS]:
        title = escape_property("Rickrolled by " + failure["test"])
        text = escape_data(f"{failure['message'][:200]} - watch {video} while you fix it")
        print(f"::error title={title}::{text}")

    append("GITHUB_STEP_SUMMARY", summary(failures, video, thumbnail))


if __name__ == "__main__":
    main()
