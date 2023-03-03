from spyne import Application, rpc, ServiceBase, String, Boolean, Array, Iterable, NullServer
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication

from services.IDM_service import IDMService

application = Application([IDMService], 'services.IDM.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)
null_server = NullServer(application, ostr=True)




if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()
