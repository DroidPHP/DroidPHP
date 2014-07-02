# Setup the development environment

The first step is to setup a good and viable development environment you'll be able to compile application for Android ARM platform.

## Install ARM Toolchain

I am using CodeSourery toolchain for cross compiling please make sure you have already installed it
Run to make sure it is properly configured

```
arm-none-linux-gnueabi-gcc -v
```
## Compiling GLibc

Download and extract Glibc and open `./glibc-2.19/resolv/resolv.h` and change `#define _PATH_RESCONF        "/etc/resolv.cof"` to `#define _PATH_RESCONF        "/data/data/org.opendroidphp/etc/resolv.conf"` and run the follwing command to cross compile glibc

> mkdir glibc_build && cd glibc_build
>../glibc-2.19/configure \
> --host=arm-none-linux-gnueabi \
> --disable-build-nscd --enable-add-ons \
> --prefix=$HOME/droidphp/glibc/usr \
> --enable-static-nss --with-tls \
> --with-ports="nptl, ports"
> make && make install

## Setup toolchain
Use to following configuration when you are cross compiling
Create a filename `crosstool.sh` and include it before whenever you need to cross compile for ARM


```
#!/bin/bash
PROJECT_BASE=$(pwd);
REPOSITORY=$PROJECT_BASE/download
ROOTFS=$PROJECT_BASE/compiled/usr
## edit this
export SYSROOT_SYS="$HOME/CodeSourcery/Sourcery_CodeBench_Lite_for_ARM_GNU_Linux/arm-none-linux-gnueabi/libc"
export SYSROOT_GLIBC="$HOME/droidphp/glibc"
export CC="arm-none-linux-gnueabi-gcc"
export CXX="arm-none-linux-gnueabi-g++"
export RANLIB="arm-none-linux-gnueabi-ranlib"
export STRIP='arm-none-linux-gnueabi-strip'
export LD='arm-none-linux-gnueabi-ld'
export AR='arm-none-linux-gnueabi-ar'
export HOST="arm-none-linux-gnueabi"
export CPPFLAGS="-I${ROOTFS}/include"
export LDFLAGS="-L${ROOFTS}/lib"
```


## Compiling Zlib

```
cd $REPOSITORY && wget http://zlib.net/zlib-1.2.8.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/zlib-1.2.8.tar.gz && cd ./zlib-1.2.8
CFLAGS="--sysroot=$SYSROOT_SYS" ./configure \
--prefix="$ROOTFS" \
--static
make && make install
```

## Compiling Bz2

```
cd $REPOSITORY && wget http://www.bzip.org/1.0.6/bzip2-1.0.6.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/bzip2-1.0.6.tar.gz && cd ./bzip2-1.0.6
make install PREFIX=$ROOTFS
```

## Compiling OpenSSL

```
cd $REPOSITORY && wget https://www.openssl.org/source/openssl-1.0.1f.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/openssl-1.0.1f.tar.gz && cd ./openssl-1.0.1f
OPENSSL_TARGET="linux-armv4"
CFLAGS="--sysroot=$SYSROOT_GLIBC -static" ./Configure \
$OPENSSL_TARGET no-shared \
--prefix="/usr" \
--openssldir="$ROOTFS" \
--with-zlib-lib="$ROOTFS/lib" \
--with-zlib-include="$ROOTFS/include" make && make INSTALL_PREFIX=$ROOTFS install
```

## Compiling CURL

```
cd $REPOSITORY && wget http://curl.haxx.se/download/curl-7.35.0.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/curl-7.35.0.tar.gz && cd ./curl-7.35.0
CC="arm-none-linux-gnueabi-gcc --sysroot=$SYSROOT_GLIBC" \
CXX="arm-none-linux-gnueabi-g++ --sysroot=$SYSROOT_GLIBC" \
CPPFLAGS="-I$ROOTFS/include" \
LDFLAGS="-L$ROOTFS/lib --static" \
./configure --prefix="$ROOTFS" \
--enable-zlib \
--disable-shared \
--without-ssl \
--enable-static \
--host=x86_64
make && make install
```

## Compiling Iconv

```
cd $REPOSITORY && wget http://ftp.gnu.org/pub/gnu/libiconv/libiconv-1.14.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/libiconv-1.14.tar.gz && cd ./libiconv-1.14
./configure \
--prefix="$ROOTFS" \
--enable-static \
--disable-shared \
--host=$HOST
make && make install
```

## Compiling ncurses

```
cd $REPOSITORY && wget ftp://invisible-island.net/ncurses/ncurses-5.9.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/ncurses-5.9.tar.gz && cd ./ncurses-5.9
./configure \
--prefix="$ROOTFS" \
--disable-widec \
--disable-ext-funcs \
--without-cxx-binding \
--without-cxx \
--without-shared \
--without-ada \
--without-tests \
--without-debug \
--host=$HOST && make && make install
```

## Compiling mhash

```
cd $REPOSITORY && wget ftp://invisible-island.net/ncurses/ncurses-5.9.tar.gz
cd $PROJECT_BASE/build && tar -xjvf $REPOSITORY/mhash-0.9.9.9.tar.bz2 && cd ./mhash-0.9.9.9
ac_cv_func_malloc_0_nonnull=yes \
CPPFLAGS="-I$ROOTFS/include" LDFLAGS="-L$ROOTFS/lib" \
./configure --prefix=$ROOTFS \
--disable-shared \
--enable-static --host=$HOST && make && make install
```

## Compiling libmcrypt

```
cd $PROJECT_BASE/build && tar -xjvf $REPOSITORY/libmcrypt-2.5.8.tar.bz2 && cd ./libmcrypt-2.5.8
ac_cv_type_unsigned_long_int=no ac_cv_sizeof_unsigned_int=no \
ac_cv_sizeof_unsigned_short_int=no ac_cv_sizeof_unsigned_char=no \
ac_cv_func_malloc_0_nonnull=yes \
ac_cv_func_realloc_0_nonnull=yes \
CPPFLAGS="-I$ROOTFS/include" LDFLAGS="-L$ROOTFS/lib" \
./configure \
--prefix=$ROOTFS \
--host=$HOST \
--with-mhash \
--enable-static \
--disable-shared
# hack to bypass Cannot find a 32,16 bit integer in your system
sed -i "s{SIZEOF_UNSIGNED_CHAR no{SIZEOF_UNSIGNED_CHAR 1{" config.h
sed -i "s{SIZEOF_UNSIGNED_INT no{SIZEOF_UNSIGNED_INT 4{" config.h
sed -i "s{SIZEOF_UNSIGNED_LONG_INT 0{SIZEOF_UNSIGNED_LONG_INT 4{" config.h
sed -i "s{SIZEOF_UNSIGNED_SHORT_INT no{SIZEOF_UNSIGNED_SHORT_INT 2{" config.h
#make clean
make && make install
```

## Compiling PCRE

```
cd $REPOSITORY && wget ftp://ftp.csx.cam.ac.uk/pub/software/programming/pcre/pcre-8.34.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/pcre-8.34.tar.gz && cd ./pcre-8.34
./configure \
--prefix="$ROOTFS" \
--disable-cpp \
--host=$HOST
make && make install
```

## Compiling libpng

```
cd $REPOSITORY && wget http://prdownloads.sourceforge.net/libpng/libpng-1.6.9.tar.gz?download
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/libpng-1.6.9.tar.gz && cd ./libpng-1.6.9
./configure \
CPPFLAGS="-mfpu=neon -I$ROOTFS/include" \
LDFLAGS="-L$ROOTFS/lib" \
--prefix=$ROOTFS \
--host=$HOST
make && make install
```

## Compiling libjpeg

```
cd $REPOSITORY && wget http://www.ijg.org/files/jpegsrc.v9.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/jpegsrc.v9.tar.gz && cd ./jpeg-9
./configure \
--prefix=$ROOTFS \
--host=$HOST
make && make install
```

## Compiling readline

```
cd $REPOSITORY && wget http://ftp.gnu.org/gnu/readline/readline-6.2.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/readline-6.2.tar.gz && cd ./readline-6.2
./configure --prefix="$ROOTFS" \
--with-curses="$ROOTFS"  \
--host=$HOST
make && make install
```

## Compiling libxml2

```
cd $REPOSITORY && wget ftp://xmlsoft.org/libxml2/libxml2-git-snapshot.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/libxml2-git-snapshot.tar.gz && cd ./libxml2-2.9.1
./configure --prefix="$ROOTFS" \
--with-readline="$ROOTFS" \
--with-iconv="$ROOTFS" \
--with-zlib="$ROOTFS" \
--without-python \
--host=$HOST
make && make install
```

## Compiling freetype

```
cd $REPOSITORY && wget http://download.savannah.gnu.org/releases/freetype/freetype-2.5.3.tar.gz
cd $PROJECT_BASE/build && tar -xzvf $REPOSITORY/freetype-2.5.2.tar.gz && cd ./freetype-2.5.2
LIBPNG_CFLAGS="-L$ROOTFS/include" \
LIBPNG_LDFLAGS="-L$ROOTFS/lib" \
CPPFLAGS="-I$ROOTFS/include" \
LDFLAGS="-L$ROOTFS/lib" \
./configure \
--prefix=$ROOTFS \
--host=$HOST && make && make install
```



## Compiling LIGHTTPD

```
cd $REPOSITORY && wget http://download.lighttpd.net/lighttpd/releases-1.4.x/lighttpd-1.4.34.tar.gz
cd $PROJECT_BASE/build && lighttpd-1.4.34.tar.gz && cd ./lighttpd-1.4.34
export CROSS_COMPILING=yes
export PCRECONFIG=$ROOTFS/bin/pcre-config
export CFLAGS="-DLIGHTTPD_STATIC -I$ROOTFS/include "
export LDFLAGS="-L$ROOTFS/lib"
#export LUA_LIBS="-L$ROOTFS/lib"
#export LUA_CFLAGS="-I$ROOTFS/include"
## hack the lighttpd build system
## manually copy `droidphp/diffs/lighttpd_embedded_arm_support.diff` to `lighttpd-1.4.xx/`
patch -p1 < lighttpd_embedded_arm_support.diff
LIBS="-lpcre -lz -ldl" \
CC="arm-none-linux-gnueabi-gcc -static --sysroot=$SYSROOT_GLIBC" ./configure \
--prefix="$ROOTFS/lighttpd" \
--disable-shared \
--enable-static \
--with-openssl="$ROOTFS/usr" \
--with-libiconv=$ROOTFS \
--disable-rpath \
--with-pcre \
--with-sysroot=$SYSROOT_GLIBC \
--with-zlib \
--without-bzip2 \
--without-lua \
--host=$HOST
make CC="arm-none-linux-gnueabi-gcc -static --sysroot=$SYSROOT_GLIBC" \
CXX="arm-none-linux-gnueabi-g++ -static --sysroot=$SYSROOT_GLIBC"
make install
```


## Compiling PHP

```
cd $REPOSITORY && wget http://www.php.net/get/php-5.5.9.tar.bz2/from/a/mirror
cd $PROJECT_BASE/build && tar -xjvf $REPOSITORY/php-5.5.9.tar.bz2 && cd ./php-5.5.9
# make clean
CC="arm-none-linux-gnueabi-gcc --sysroot=$SYSROOT_GLIBC" \
CXX="arm-none-linux-gnueabi-g++ --sysroot=$SYSROOT_GLIBC" \
LDFLAGS="-static -I$ROOTFS/lib -L$ROOTFS/usr/lib" \
CFLAGS="-I$ROOTFS/include -I$ROOTFS/usr/include" ./configure \
--prefix=$ROOTFS/php \
--enable-static \
--disable-all \
--enable-filter \
--enable-calendar \
--enable-ctype \
--enable-dom \
--enable-exif \
--enable-fileinfo \
--enable-ftp \
--with-mhash="$ROOTFS/usr" \
--disable-intl \
--disable-phar \
--enable-posix \
--enable-shmop \
--enable-simplexml \
--enable-sysvmsg \
--enable-sysvsem \
--enable-tokenizer \
--disable-wddx \
--enable-xmlreader \
--enable-xmlwriter \
--enable-opcache=no \
--enable-pcntl \
--enable-soap \
--enable-cgi \
--enable-json \
--with-zlib \
--enable-zip \
--with-mysql \
--enable-mysqlnd \
--with-mysqli=mysqlnd \
--enable-pdo \
--with-pdo-mysql=mysqlnd \
--enable-libxml \
--with-pdo-sqlite \
--with-sqlite3 \
--enable-sockets \
--enable-bcmath \
--enable-mbstring \
--enable-mbregex \
--enable-session \
--with-zlib-dir="$ROOTFS" \
--with-libxml-dir="$ROOTFS" \
--with-curl="$ROOTFS" \
--with-openssl="$ROOTFS/usr" \
--with-jpeg-dir="$ROOTFS" \
--with-png-dir="$ROOTFS" \
--with-freetype-dir="$ROOTFS" \
--with-iconv-dir="$ROOTFS" \
--with-mcrypt="$ROOTFS" \
--host=$HOST
## hack
sed -ie "s{ext/mysqlnd/php_mysqlnd_config.h{config.h{" ext/mysqlnd/mysqlnd_portability.h
sed -i "s{-export-dynamic{-all-static{" Makefile
sed -i "s{-I/usr/include{ {" Makefile
make && make install
```

## Compiling MSMTP

```
cd $REPOSITORY && wget http://www.php.net/get/php-5.5.9.tar.bz2/from/a/mirror
cd $PROJECT_BASE/build && tar -xjvf $REPOSITORY/msmtp-1.4.31.tar.bz2 &&cd ./msmtp-1.4.31
ac_cv_sys_file_offset_bits=unknown \
libssl_CFLAGS="-I$ROOTFS/usr/include" \
libssl_LDFLAGS="-L$ROOTFS/usr/lib" \
libssl_LIBS="-lssl -lcrypto" \
CPPFLAGS="-I$ROOTFS/include -I$ROOTFS/usr/include" \
LDFLAGS="-L$ROOTFS/lib -L$ROOTFS/usr/lib" LIBS="-ldl" ./configure \
--prefix=$ROOTFS/msmstp \
--host=$HOST \
--with-libiconv-prefix="$ROOTFS" \
--with-ssl=openssl
make CFLAGS="-static" \
CC="arm-none-linux-gnueabi-gcc --sysroot=$SYSROOT_GLIBC" \
CPP="arm-none-linux-gnueabi-g++ --sysroot=$SYSROOT_GLIBC" && make install
```

## Author

* [Shushant Kumar](http://github.com/shushant)

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/droidphp/droidphp/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.
