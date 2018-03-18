import mysql.connector


def __start__():
    print('Starting insertion.')
    _connector = mysql.connector.connect(user='producer', password='producerdb1123@', host='207.154.192.177', database='BILLING_RECORDS', port=23306)
    statement = 'SELECT count\(*\) FROM customers;'
    try:
        _connector.cmd_query(statement)
    except mysql.connector.errors.ProgrammingError:
        print('Table does not exist. Creating...')
        _statement = 'CREATE TABLE \'customers\' \(' \
                     'ID int,' \
                     'FirstName varchar\(64\),' \
                     'LastName varchar\(64\),' \
                     'Address varchar\(256\),' \
                     'Fee DECIMAL\(5,2\)' \
                     '\)\;'  #add more if necessary
        _connector.cmd_query(_statement)

    _connector.close()


def __generate__():
    pass


if __name__ == '__main__':
    __start__()
else:
    print('Not a module')

