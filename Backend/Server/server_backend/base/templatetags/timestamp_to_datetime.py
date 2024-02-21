from django import template
register = template.Library()

@register.filter('timestamp_to_datetime')
def convert_timestamp_to_datetime(timestamp):
    from datetime import datetime
    return datetime.fromtimestamp(timestamp)