# camel-kafka-ai-ml-demo

Deployment instructions

1. Deploy the consumer.

    ```
    oc new-app \
      --name=kafka-consumer \
      java:8~https://PATH_TO_THIS_REPO
    ```

1. Set environment from config map and secret.

    ```
    oc set env dc/kafka-consumer --from=cm/mlflow-cm

    oc set env dc/kafka-consumer --from=secret/kie-admin --prefix='BRMS_'
    ```