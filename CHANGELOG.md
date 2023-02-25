# Changes

1.6.0
----------
- Persist optOut changes.
- Default console logging to true.
- Default log level to trace.
- Unit testing to API 33.

1.5.0
----------
- Add ability to force config synchronization.
- Add multi agent support.
- Add additional debug logging.

1.4.0
----------
- Improve json serialization performance of source.
- Add ability to toggle console logging.
- Add ability to toggle remote logging.
- Add ability to toggle synchronous logging.
- Add ability to override log level locally.

1.3.0
----------
- Adds GZIP compression.
- Improve password scrubber for JSON.

1.2.1
----------
- Fixes incomplete log lines under certain edge cases.

1.2.0
----------
- Add encryption at rest

1.1.1
----------
- Remove buffered writer to favor immediate writing
- Improve java api

1.1.0
----------
- Added default log scrubbers (emails and passwords)
- Stop logger when disk is full

1.0.2
----------
- Fixes daily log trimming 
- Threading is now down to 2 threads instead of 3.
- Adds stress test to demo app that has 4 threads logging simultaneously at a high volume.

1.0.1
----------
- Initial release