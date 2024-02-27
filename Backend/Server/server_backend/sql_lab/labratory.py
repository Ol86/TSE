import json
from json import JSONEncoder

import openai
from openai import OpenAI
import os

openai.api_key = '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'
with open('database.json', 'r') as f:
    database = f.read()
messages = [
    {"role": "system", "content": "You create SQL Queries from Natural Language over a database that contains 2 tables."
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

# print(translating("give me the two tables combined"))

#TODO Usernames einbauen?
"""
Here will be the Prototype of the complete JSON

A Table will always follow this format:
    {
     "name": "table1",
       "columns": [
         {
           "name": "",
           "type": ""
         },
         {
           "name": "",
           "type": ""
         },
         {
           "name": "",
           "type": ""
         }
         
       ]
       "primary key": ""
    }

"""