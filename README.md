[Repository](https://github.com/pallet/meteor-crate) &#xb7;
[Issues](https://github.com/pallet/meteor-crate/issues) &#xb7;
[API docs](http://palletops.com/meteor-crate/0.8/api) &#xb7;
[Annotated source](http://palletops.com/meteor-crate/0.8/annotated/uberdoc.html) &#xb7;
[Release Notes](https://github.com/pallet/meteor-crate/blob/develop/ReleaseNotes.md)

A [pallet](http://palletops.com/) crate to install and configure
 [meteor](http://meteor.com).

### Dependency Information

```clj
:dependencies [[com.palletops/meteor-crate "0.8.0-alpha.2"]]
```

### Releases

<table>
<thead>
  <tr><th>Pallet</th><th>Crate Version</th><th>Repo</th><th>GroupId</th></tr>
</thead>
<tbody>
  <tr>
    <th>0.8.0-RC.4</th>
    <td>0.8.0-alpha.2</td>
    <td>clojars</td>
    <td>com.palletops</td>
    <td><a href='https://github.com/pallet/meteor-crate/blob/0.8.0-alpha.2/ReleaseNotes.md'>Release Notes</a></td>
    <td><a href='https://github.com/pallet/meteor-crate/blob/0.8.0-alpha.2/'>Source</a></td>
  </tr>
</tbody>
</table>

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

## License

Copyright (C) 2012, 2013 Hugo Duncan

Distributed under the Eclipse Public License.
