#!/bin/bash
echo "============================================================="
echo " Mostrando logs en vivo del sistema..."
echo " Presiona Ctrl+C para salir"
echo "============================================================="
cd "$(dirname "$0")"
docker compose logs -f
