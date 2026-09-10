import json
import os
import boto3
import uuid

# Cliente de DynamoDB y SQS
dynamodb = boto3.resource('dynamodb')
sqs = boto3.client('sqs')

table_name = os.environ.get('DYNAMODB_TABLE', 'api-usuarios-serverless-prod-usuarios')
queue_url = os.environ.get('SQS_QUEUE_URL')
table = dynamodb.Table(table_name)

def build_response(status_code, body):
    return {
        "statusCode": status_code,
        "headers": {
            "Content-Type": "application/json"
        },
        "body": json.dumps(body)
    }

def crear(event, context):
    try:
        body = json.loads(event.get('body', '{}'))
        
        # Validar campos básicos
        if not body.get('nombre') or not body.get('correo'):
            return build_response(400, {"error": "nombre y correo son obligatorios"})
        
        # Generar un ID único
        usuario_id = str(uuid.uuid4())
        
        item = {
            'id': usuario_id,
            'nombre': body['nombre'],
            'correo': body['correo']
        }
        
        # Guardar en DynamoDB
        table.put_item(Item=item)
        
        # Enviar mensaje a SQS
        if queue_url:
            mensaje = {
                "evento": "USUARIO_CREADO",
                "usuario": item
            }
            sqs.send_message(
                QueueUrl=queue_url,
                MessageBody=json.dumps(mensaje)
            )
            print(f"Mensaje enviado a SQS: {mensaje}")
        
        return build_response(201, {"message": "Usuario creado con éxito", "id": usuario_id})
        
    except Exception as e:
        print("Error:", e)
        return build_response(500, {"error": "Error interno del servidor"})


def obtener(event, context):
    try:
        path_parameters = event.get('pathParameters', {})
        usuario_id = path_parameters.get('id')
        
        if not usuario_id:
            return build_response(400, {"error": "Falta el parámetro id"})
            
        # Consultar DynamoDB
        response = table.get_item(Key={'id': usuario_id})
        item = response.get('Item')
        
        if not item:
            return build_response(404, {"error": "Usuario no encontrado"})
            
        return build_response(200, item)
        
    except Exception as e:
        print("Error:", e)
        return build_response(500, {"error": "Error interno del servidor"})
