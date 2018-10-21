import sys
import requests
import json
import base64
# from google.cloud import automl


def get_prediction(content, project_id, model_id):
    # prediction_client = automl.PredictionServiceClient()

    # name = 'projects/{}/locations/us-central1/models/{}'.format(project_id, model_id)
    print(content[:10])
    payload = {'image': {'image_bytes': base64.b64encode(content).decode('utf-8')}}
    # params = {}
    # request = prediction_client.predict(name, payload, params)
    r2 = requests.post('http://localhost:5000/image_detect', data=json.dumps(payload), headers={'Content-type': 'application/json'})
    print(r2.json())
    # return request  # waits till request is returned


if __name__ == '__main__':
    file_path = sys.argv[1]
    project_id = sys.argv[2]
    model_id = sys.argv[3]

    with open(file_path, 'rb') as ff:
        content = ff.read()

    print(get_prediction(content, project_id, model_id))
