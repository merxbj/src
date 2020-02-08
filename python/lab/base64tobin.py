import base64

base64.decode(open("encoded.b64"), open("decoded.cer", "bw+"))