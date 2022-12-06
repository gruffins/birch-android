# Changes

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