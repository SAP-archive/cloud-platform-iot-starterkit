all:
	protoc --python_out=. mms_downstream.proto
	python2.7 poll_pushed_messages_via_protobuf.py

clean:
	rm -f mms_downstream_pb2.py *.pyc
