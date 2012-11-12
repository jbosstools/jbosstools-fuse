# Fuse Web Console

The Fuse Web Console provides a web based console for working with Fuse

## Building with GruntJS

When developing the console, the most RAD tool for building the client side is [gruntjs](http://gruntjs.com/)

### Installing GruntJS

To build the code with gruntjs you will need to install [npm](https://npmjs.org/) e.g. by [installing nodejs](http://nodejs.org/)

Then to install grunt:

    npm install -g grunt

Then in the web-console directory you will need to install the grunt plugins required

    cd web/web-console
    npm install grunt-type

### Building with GruntJS

Its a simple matter of running 'grunt' :) By default this then watches for changes to the source files and auto-recompiles on the fly

    grunt


