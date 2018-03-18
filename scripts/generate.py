import mysql.connector


def _start():
    print('Hello World')
    _connector = mysql.connector.connect(user='producer', password='producerdb1123@', host='207.154.192.177', database='BILLING_RECORDS', port=23306)
    _connector.close()


if __name__ == '__main__':
    _start()
else:
    print('Not a module')

