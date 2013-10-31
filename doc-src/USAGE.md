## Usage

The meteor crate provides a `server-spec` function that returns a
server-spec. This server spec will install and run the meteor server.
You pass a map of options to configure meteor.

The `server-spec` provides an easy way of using the crate functions, and you can
use the following crate functions directly if you need to.

The `settings` function provides a plan function that should be called in the
`:settings` phase.  The function puts the configuration options into the pallet
session, where they can be found by the other crate functions, or by other
crates wanting to interact with meteor.

The `install` function is responsible for actually installing meteor.

## Live test on vmfest

For example, to run the live test on VMFest, using Ubuntu 13.04:

```sh
lein with-profile +vmfest pallet up --selectors ubuntu-13
lein with-profile +vmfest pallet down --selectors ubuntu-13
```
