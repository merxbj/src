__author__ = 'merxbj'


DOW = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]


def format_duration(duration):
    return '{0:02}:{1:02}:{2:02}'.format(int(duration.total_seconds()) // 3600,
                                         (int(duration.total_seconds()) % 3600) // 60,
                                         (int(duration.total_seconds()) % 3600) % 60)
