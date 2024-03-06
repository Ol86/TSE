import json
from json import JSONEncoder

import openai
from openai import OpenAI
import os

"""Here is the main connection of the sql laboratory to OpenAI
"""
openai.api_key = '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'
#TODO korrekten Pfad
with open('./sql_lab/database.json', 'r') as f:
    database = f.read()
messages = [
    {"role": "system", "content": "You create SQL Queries from Natural Language over a database that contains 12 tables."
                                  "Only give back SQL-Queries nothing else. Database: " + database
                                  }
]
# todo großen JSON string auslagern!

client = OpenAI(
    api_key='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'
)

chat_completion = client.chat.completions.create(
    model="gpt-3.5-turbo",
    messages=messages
)


# print(chat_completion.choices[0].message.content)

def translating(message):
    """In this method the user input gets connected to GPT-3.5

    :param message: The user input
    """
    mes = str(message)
    messages.append(
        {"role": "user", "content": mes},
    )
    chat_completions = client.chat.completions.create(
        messages=messages,
        model="gpt-3.5-turbo"
    )
    reply = chat_completions.choices[0].message
    content = str(reply.content)
    messages.append(
        {"role": "system", "content": content},
    )
    return reply