# csprog2020 group18

## Warning
お金がないので私の PC がスコアサーバーとなっています。固定 IP でも無いため、`ngrok` で外部公開をするようにしています。  
もしもあなたが本当にこのゲームをやりたいのであれば私にコンタクトを取ってください。mysql サーバーの IP を渡します。

## Requirements
+ make
```console
$ sudo apt install build-essential
$ make --version
```

+ javac, java
```console
$ sudo apt install default-jdk
$ javac --version
$ java --version
```

+ openjfx
```console
$ sudo apt install openjfx
$ dpkg -L openjfx
```

+ libmariadb-java とか
```console
$ sudo apt install libmariadb-java
$ wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.23.zip
$ unzip mysql-connector-java-8.0.23.zip
```

## Commands
基本的にコンパイルや実行などは `make` で行ってください。そっちのほうが楽ですし、メンバー全員で統一することができます。

### Run
```console
$ make run
```

### 画像元
+ http://dots-design.com/2008/12/1-3.html
+ https://hpgpixer.jp/image_icons
+ 