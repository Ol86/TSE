import json
from json import JSONEncoder

import openai
from openai import OpenAI
import os

openai.api_key = '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'

messages = [
    {"role": "system", "content": "You create SQL Queries from Natural Language over a database that contains 2 tables."
                                  "Only give back SQL-Queries nothing else "
                                  """{
  "tables": {
    "heritage": {
      "columns": ["name", "age", "city"],
      "primary_key": "name"
    },
    "city_info": {
      "columns": ["number_of_inhabitants", "city", "postal_code"],
      "primary_key": "city"
    }
  }
}"""}
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
           "name": "column1",
           "type": "string"
         },
         {
           "name": "column2",
           "type": "integer"
         },
         {
           "name": "column3",
           "type": "float"
         }
       ]}

       

{
  "database": {
  "tables": [
  {
    "name": "experiment,
    "cloumns":  [
    {
      "name": "experiment_id"
      "type": "integer"
      }
      {
      "name": "title"
      "type": "string"
      }
      {
      "name": "questions"
      "type": "Question"
      }
      {
      "name": "accelerometer"
      "type": "boolean"
      }
      {
      "name": "heart_rate"
      "type": "boolean"
      }
      {
      "name": "ppg_green"
      "type": "boolean"
      }
      {
      "name": "ppg_ir"
      "type": "boolean"
      }
      {
      "name": "ppg_red"
      "type": "boolean"
      }
      {
      "name": "bia"
      "type": "boolean"
      }
      {
      "name": "ecg"
      "type": "boolean"
      }
      {
      "name": "spo2"
      "type": "boolean"
      }
      {
      "name": "sweat_loss"
      "type": "boolean"
      }
      {
      "name": "watches"
      "type": "Watch"
      }
      {
      "name": "created_by"
      "type": "integer"
      }
      {
      "name": "created_at"
      "type": "timestamp"
    }
    ]
    "primary key": "experiment_id"
  }
  ]
}

"""