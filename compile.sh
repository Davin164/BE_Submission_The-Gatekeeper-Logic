#!/bin/bash

echo "Compiling Java files..."

# Hapus folder bin lama
rm -rf bin
mkdir -p bin

# Compile berurutan sesuai dependency
echo "Compiling models..."
javac -cp ".;lib/*" -d bin src/models/*.java

echo "Compiling config..."
javac -cp ".;lib/*;bin" -d bin src/config/*.java

echo "Compiling dao..."
javac -cp ".;lib/*;bin" -d bin src/dao/*.java

echo "Compiling services..."
javac -cp ".;lib/*;bin" -d bin src/services/*.java

echo "Compiling controllers..."
javac -cp ".;lib/*;bin" -d bin src/controllers/*.java

echo "Compiling server..."
javac -cp ".;lib/*;bin" -d bin src/server/*.java

echo "Compiling Main..."
javac -cp ".;lib/*;bin" -d bin src/Main.java

echo "✓ Compilation complete!"