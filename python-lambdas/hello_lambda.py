import json

def handler(event, context):
    """
    Lambda function handler
    - event: Contiene los datos de la petición (como body, headers, parámetros).
    - context: Contiene información sobre el entorno de ejecución de la Lambda.
    """
    print("Recibí un evento:", event)
    
    # Extraer datos de ejemplo del body
    body = {}
    if "body" in event and event["body"]:
        try:
            body = json.loads(event["body"])
        except Exception:
            pass
            
    name = body.get("name", "Mundo")

    return {
        "statusCode": 200,
        "headers": {
            "Content-Type": "application/json"
        },
        "body": json.dumps({
            "message": f"¡Hola {name} desde AWS Lambda y Python!",
            "status": "success"
        })
    }
