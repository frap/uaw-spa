(ns uaw-spa.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [uaw-spa.core-test]))

(doo-tests 'uaw-spa.core-test)
