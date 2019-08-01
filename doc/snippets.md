# Compute a m5d hash with Python 3
More info <https://docs.python.org/3.6/library/hashlib.html#hash-algorithms>:
```
import hashlib
example = b'this is an example string'
hashlib.md5(example).hexdigest()
```

Test the same string with bash. Mind the '-n' flag: avoids echo from generating an '\n' symbol. More info <https://stackoverflow.com/q/5693360>:
```
echo -n 'this is an example string' | md5sum
```

# Count CPU cores with Python 3
```
import multiprocessing
multiprocessing.cpu_count()
```

# Create AWS bucket via cli
NEW_BUCKET_NAME='a-new-bucket'
aws s3 mb "s3://$NEW_BUCKET_NAME"
