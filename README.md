# clj-props

A Clojure library for reading property files

## Usage

First add the following dependency in your `project.clj`,

```clojrue
[cassc/clj-props "0.1.2"]
```

And use as
```clojure
(require 'clj-props.core)
(defconfig props (clojure.java.io/file "config.edn") {:secure false})
(props [:log-level] {:default :info})
```

When the config file is modified, props automatically reloads itself.

## License

Copyright © 2016 CL

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
