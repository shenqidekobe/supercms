var requirejsCompileSkip = require('./tasks/requirejs-compile-skip.json');
var pkg = require('./package.json');
var pub = pkg.smartadmin.public;
var tmp = pkg.smartadmin.temp;
var bld = pkg.smartadmin.build;

module.exports = function (grunt) {
    // Project configuration.
    grunt.initConfig({
    	//  switch directive/controller/factory files from lazy-loading to regular angular declarations (is required for annotation)
        turnOffPotatoDeclaration: {
            tmp: {
                expand: true,
                src: [
                    tmp + '*/**/*.js',
                    tmp + 'app.js'
                ]
            }
        },
        // without this step minification is impossible
        ngAnnotate: {
            tmp: {
                expand: true,
                src: [
                    tmp + '*/**/*.js',
                    tmp + 'app.js'
                ],
                ext: '.js', // Dest filepaths will have this extension.
                extDot: 'last'
            }
        },
        // switch back to lazy-loading
        turnOnPotatoDeclaration: {
            tmp: {
                expand: true,
                src: [
                    tmp + '*/**/*.js',
                    tmp + 'app.js'
                ]
            }
        },
        // adjust templateUrl values: from 'app/modules/misc/lockscreen.html' to 'build/modules/misc/lockscreen.html'
        adjustTemplateUrls: {
            tmp: {
                options: {
                    from: 'app',
                    to: 'build'
                },
                expand: true,
                src: [
                    tmp + '*/**/*.*',
                    tmp + 'app.js'
                ]
            }
        },
        // concatenate all *.tpl.html template to one angular module 
        html2js: {
            options: {
                base: tmp,
                module: 'smart-templates',
                singleModule: true,
                rename: function (moduleName) {
                    return 'build/' + moduleName;
                }
            },
            main: {
                src: [tmp + '**/*.tpl.html'],
                dest: tmp + 'smart-templates.js'
            }
        },
        // additional includes for build (templates from prev step)
        addIncludes:{
            options:{
                appFile: tmp + 'app.js',
                includesFile: tmp + 'includes.js'
            },
            templates:{
                options:{
                    angularModule: true,
                    wrapToDefine: true,
                    name: 'smart-templates',
                    injectToApp: true
                },
                src: [
                    tmp + 'smart-templates.js'
                ]
            }

        },
        // minification
        uglify: {
            tmp: {
                expand: true,
                cwd: tmp,
                src: [
                    '**/*.js'
                ],
                dest: tmp,
                ext: '.js'
            }
        },
        // erase temp directory
        clean: {
            pre: {
                options: {
                    force: true
                },
                src: [
                    bld,
                    tmp
                ]
            },
            post: {
                options: {
                    force: true
                },
                src: [
                    tmp
                ]
            }
        },
        // copy lazy minified files to build dir
        copy: {
            pre: {
                expand: true,
                cwd: pub + 'app/',
                src: [
                    '**'
                ],
                dest: tmp
            },
            post: {
                expand: true,
                cwd: tmp,
                src: [
                    '*/**',
                    'rconfig.js',
                    '!**/*.tpl.html'
                ],
                dest: bld
            }
        },
        // r.js on temp/main.js to build/main.js dir. 
        // requirejs-compile-skip.json is used to exclude files from build
        // (useful for cdn and reduce compiled file.If not used, all required in main.js plugin files will be included to build file)
        requirejs: {
            compile: {
                options: {
                    baseUrl: tmp,
                    paths: requirejsCompileSkip,
                    mainConfigFile: tmp + 'rconfig.js',
                    name: "main",
                    optimize: 'none',
                    uglify2: {
                        mangle: false
                    },
                    out: bld + 'main.js',
                    done: function (done, output) {
                        console.log('done requirejs');
                        done();
                    }
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-ng-annotate');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-requirejs');
    grunt.loadNpmTasks('grunt-html2js');
    grunt.loadTasks('tasks');
    
    grunt.registerTask('default', [
        'clean:pre',
        'copy:pre',
        'turnOffPotatoDeclaration',
        'ngAnnotate:tmp',
        'turnOnPotatoDeclaration',
        'adjustTemplateUrls',
        'html2js',
        'addIncludes',
        'uglify',
        'requirejs',
        'copy:post',
        'clean:post'
    ]);
    grunt.registerTask('vtp', [
        'vendor-to-plugin',
        'default'
    ]);
};