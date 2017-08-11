all:
	protoc --python_out=. mms_upstream.proto
	python2.7 post_upstream.py

clean:
	rm -f mms_upstream_pb2.py *.pyc
