Input a URL with optional depth, workers, and time limit amounts to launch a wec crawler that will visit the provided URL, document the title, and do so for other URLs found on that page, for URLs found on those pages, and so on. The links and titles can then be exported as a file.

This takes advantage of threading in Java as well as thread-safe hash tables and queues.

Done as part of the JetBrains Academy/Hyperskill Java curriculum.
