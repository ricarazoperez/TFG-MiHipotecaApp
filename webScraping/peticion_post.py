import requests
import json

# Define los datos de la solicitud en formato JSON
datos = {
    "comunidad_autonoma": "Álava",
    "tipo_vivienda": "Comprar mi residencia habitual",
    "antiguedad_vivienda": "nueva",
    "precio_vivienda": 350000,
    "cantidad_abonada": 95000,
    "plazo_anios": 20,
    "ingresos": 4000,
    "detalles": True
}

# Define las cabeceras de la solicitud
headers = {'Content-Type': 'application/json'}

# Envía la solicitud POST a la API Flask
response = requests.post('http://147.96.81.245:5000/pruebaArray', json=datos, headers=headers)

# Imprime la respuesta de la API Flask
print(response.json())
