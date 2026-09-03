import json
from hello_lambda import handler

# Simulamos un evento proveniente de AWS API Gateway
mock_event = {
    "body": json.dumps({"name": "Camilo"})
}

# Simulamos el contexto (usualmente proveído por AWS)
mock_context = {}

print("Iniciando prueba local de Lambda...\n")

# Ejecutamos la función como lo haría AWS
response = handler(mock_event, mock_context)

print("\n--- Respuesta generada ---")
print(f"Status Code: {response['statusCode']}")
print("Body:", response['body'])
