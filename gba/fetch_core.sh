#!/usr/bin/env bash
# Baixa o núcleo mGBA (libretro) para o módulo GBA. Rode antes de compilar.
set -euo pipefail
cd "$(dirname "$0")"
for abi in arm64-v8a armeabi-v7a; do
  mkdir -p "src/main/jniLibs/$abi"
  tmp="$(mktemp -d)"
  curl -fL "https://buildbot.libretro.com/nightly/android/latest/$abi/mgba_libretro_android.so.zip" -o "$tmp/core.zip"
  unzip -o "$tmp/core.zip" -d "$tmp"
  mv "$tmp/mgba_libretro_android.so" "src/main/jniLibs/$abi/libmgba_core.so"
  rm -rf "$tmp"
done
echo "Núcleo mGBA instalado em gba/src/main/jniLibs"
