import json
import os
import boto3

sns = boto3.client('sns')
topic_arn = os.environ.get('SNS_TOPIC_ARN')

def procesar(event, context):
    """
    Este handler es invocado automáticamente por SQS.
    El `event` contiene los 'Records' (mensajes) de la cola.
    """
    for record in event.get('Records', []):
        try:
            # El cuerpo del mensaje viene en record['body']
            mensaje = json.loads(record['body'])
            print("Procesando mensaje desde SQS:", mensaje)
            
            # Formateamos el mensaje para SNS (por ejemplo un correo)
            usuario = mensaje.get('usuario', {})
            email_body = f"¡Hola {usuario.get('nombre')}! Tu cuenta {usuario.get('correo')} ha sido creada exitosamente en nuestro sistema Serverless."
            
            # Publicar en SNS
            if topic_arn:
                sns.publish(
                    TopicArn=topic_arn,
                    Subject="¡Bienvenido a nuestra plataforma!",
                    Message=email_body
                )
                print(f"Notificación SNS publicada para: {usuario.get('correo')}")
                
        except Exception as e:
            print("Error al procesar mensaje SQS:", e)
            # Levantar el error haría que el mensaje regrese a SQS (o a una DLQ si existiera)
            raise e
            
    return {"statusCode": 200, "body": "Mensajes procesados exitosamente"}
