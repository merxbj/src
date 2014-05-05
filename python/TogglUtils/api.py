import requests
from pprint import pprint

me = {'since': 1399321453, 'data': {'record_timeline': False, 'should_upgrade': False, 'jquery_timeofday_format': 'h:i A', 'jquery_date_format': 'm/d/Y', 'beginning_of_week': 1, 'store_start_and_stop_time': True, 'invitation': {}, 'language': 'en_US', 'timezone': 'Europe/Prague', 'send_timer_notifications': True, 'sidebar_piechart': True, 'manual_mode': False, 'timeline_enabled': False, 'created_at': '2012-09-20T08:56:47+00:00', 'render_timeline': False, 'image_url': 'https://065494046bb48f738931-84df0e1113befc62b4cd3c4f552a8913.ssl.cf2.rackcdn.com/0dc38941fb21c25fc75cc6dae8a2a48d.jpg', 'new_blog_post': {'title': 'Toggl Button Integration With Google Drive And More', 'category': 'Uncategorized', 'url': 'http://blog.toggl.com/2014/05/toggl-button-now-works-inside-google-drive-producteev/?utm_source=rss&utm_medium=rss&utm_campaign=toggl-button-now-works-inside-google-drive-producteev', 'pub_date': '2014-05-01T05:49:53Z'}, 'timeofday_format': 'h:mm A', 'last_blog_entry': 'http://blog.toggl.com/2014/04/help-us-pick-winner-toggl-teams-video-challenge/?utm_source=rss&utm_medium=rss&utm_campaign=help-us-pick-winner-toggl-teams-video-challenge', 'fullname': 'Jaroslav Merxbauer', 'send_weekly_report': False, 'at': '2014-05-04T10:04:45+00:00', 'default_wid': 288359, 'show_offer': False, 'share_experiment': False, 'id': 367921, 'duration_format': 'classic', 'workspaces': [{'rounding': 1, 'projects_billable_by_default': True, 'admin': True, 'name': 'Default', 'at': '2013-07-05T10:12:25+00:00', 'only_admins_see_billable_rates': False, 'ical_url': '/ical/workspace_user/f0fcb64f9f43401a318f565ee609735b', 'id': 288359, 'default_currency': 'USD', 'default_hourly_rate': 125, 'rounding_minutes': 15, 'premium': True, 'only_admins_may_create_projects': False, 'only_admins_see_team_dashboard': False, 'logo_url': 'https://assets.toggl.com/images/workspace.jpg'}], 'date_format': 'MM/DD/YYYY', 'achievements_enabled': False, 'retention': 9, 'used_next': True, 'api_token': '84a975312e9cf2f028037f5f292133ce', 'send_product_emails': True, 'email': 'merxbj@gmail.com', 'openid_enabled': True, 'openid_email': 'merxbj@gmail.com', 'reports_overlay_experiment': False, 'timeline_experiment': True}}

if not me:
    headers = {'content-type': 'application/json'}
    response = requests.get('https://www.toggl.com/api/v8/me',
                            auth=('84a975312e9cf2f028037f5f292133ce', 'api_token'),
                            headers=headers)
    me = response.json()

pprint(me['data']['workspaces'][0]['id'])

