#!/bin/bash
echo "====================================================================="
echo " Deteniendo Plataforma de Cursos Online"
echo "====================================================================="
echo ""
echo "Deteniendo y eliminando contenedores..."
cd "$(dirname "$0")"
docker compose down
if [ $? -ne 0 ]; then
    echo "ERROR: No se pudo detener el sistema."
    read -n 1 -s -r -p "Presiona Enter para salir..."
    exit 1
fi
echo ""
echo "====================================================================="
echo "  Sistema detenido correctamente."
echo "  Los datos de MySQL persisten en el volumen de Docker."
echo "====================================================================="
