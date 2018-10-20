import io
import os
import sys

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types

def main():
    if len(sys.argv) < 2:
        print("Pass a filename")
        exit()
    # Instantiates a client
    client = vision.ImageAnnotatorClient()

    # The name of the image file to annotate
    file_name = os.path.join(
        os.path.dirname(__file__),
        sys.argv[1])

    # Loads the image into memory
    with io.open(file_name, 'rb') as image_file:
        content = image_file.read()

    image = types.Image(content=content)

    # Performs label detection on the image file
    response = client.label_detection(image=image)
    labels = response.label_annotations




    print('Labels:')
    #print (labels)
    for label in labels:
        print(label)
        # recType = findType(label)
        # if(recType):
        #     print(findType(label))
        # pass
        # print("label: %s, type: %s, " %(label.description, findType(label)))

def findType(label):
    plasticKeywords = ["plastic"]
    paperKeywords = ["paper"]
    landfillKeywords = ["landfill", "product"]
    keywordArray = [plasticKeywords, paperKeywords, landfillKeywords]
    for synonyms in keywordArray:
        for synonym in synonyms:
            #print("synonym: %s, description: %s, " %(synonym, label.description))
            if (synonym in label.description):
                recycleType = synonyms[0]
                return recycleType


main()