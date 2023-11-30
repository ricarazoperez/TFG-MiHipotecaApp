import requests
import json

# Define los datos de la solicitud en formato JSON
params = {
    "comunidad_autonoma": "Granada",
    "tipo_vivienda": "Comprar mi residencia habitual",
    "antiguedad_vivienda": "nueva",
    "precio_vivienda": 250000,
    "cantidad_abonada": 85000,
    "plazo_anios": 20,
    "ingresos": 4000,
    "detalles": "true"
}

# Define las cabeceras de la solicitud
headers = {'Content-Type': 'application/json'}

# Envía la solicitud POST a la API Flask
response = requests.post('http://147.96.81.245:5000/pruebaArray', params=params)

# Imprime la respuesta de la API Flask
print(response.json())
