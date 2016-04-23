function ClipData(clipEvent, dataTransfer) {
	this.event = clipEvent;
	this.dataTransfer = dataTransfer;
	this.types = findTypes(dataTransfer);
	this.hasAttachment = containsAttachment(dataTransfer);
	this.isImage = findImage(this.types);
	this.fileName = findFileName(dataTransfer);

	this.byteData = null;
	this.base64Data = null;
	this.dataUrl = null;
	/**
	 * image/png
	 */
	this.file = null;
	var _this = this;
	this.getFileExtension = function() {
		if (_this.file) {
			if (_this.file.name) {
				var idx = _this.file.name.lastIndexOf('.');
				if (idx != -1) {
					return _this.file.name.substring(idx);
				}
			}
			var tp = _this.file.type;
			var idx = tp.lastIndexOf('/');
			if (idx != -1) {
				return "." + tp.substring(idx + 1);
			}
		}
		for (var i = 0; i < _this.types.length; ++i) {
			var tp = _this.types[i];
			var idx = tp.lastIndexOf('/');
			if (idx != -1) {
				return "." + tp.substring(idx + 1);
			}
		}
		return "";
	}
	this.waitForBinaryData = function(callback) {
		findBinaryData(dataTransfer, function(file, data, url) {
//			console.debug("bindata found: " + url + "; " + data);
			if (url) {
				_this.dataUrl = url;
			}
			if (data) {
				_this.file = file;
				_this.byteData = data;
				_this.base64Data = arrayBufferToBase64(data);
				callback(_this);
			}
		});
	};
	this.dumpEvent = function() {
		var cd = this.event.clipboardData;
		var items = cd.items;
		var itemlength = -1;
		if (items) {
			itemlength = items.length;
		}
		console.debug("twedit_paste: items: " + itemlength + "; files: " + cd.files.length);
		if (items) {
			for (var i = 0; i < cd.items.length; ++i) {
				var dataTransferItem = cd.items[i];
				var type = dataTransferItem.type;
				var value = null;
				var kind = dataTransferItem.kind;
				var file;
				if ("string" == kind) {
					value = dataTransferItem.getAsString(function(sval) {
						value = sval;
						console.debug("twedit_paste: clipvalue: " + sval);
					});
				} else if ("file" == kind) {
					file = dataTransferItem.getAsFile();
					var reader = new FileReader();
					reader.onload = (function() {
						console.debug("file readed as url: " + reader.result);
						this.dataUrl = reader.result;
					});
					reader.readAsDataURL(file);
					reader = new FileReader();
					reader.onload = (function() {
						this.byteData = reader.result;
						this.base64Data = arrayBufferToBase64(reader.result);
						console.debug("file readed as bytes: " + arrayBufferToBase64(reader.result));
					});
					reader.readAsArrayBuffer(file);
				}
			}
			console.debug("  type: " + type + "; " + kind + "; " + value);
			if (file) {
				console.debug("  file: " + file.name + "; " + file.length);
			}
		}
		for (var i = 0; i < cd.files.length; ++i) {
			var file = cd.files[i];
			console.debug("  file: " + file.name + "; " + file.length);
			var reader = new FileReader();
			reader.onload = (function() {
				var reader = new FileReader();
				reader.onload = (function() {
					console.debug("file readed as url: " + reader.result);
					this.dataUrl = reader.result;
				});
				reader.readAsDataURL(file);
				reader = new FileReader();
				reader.onload = (function() {
					this.byteData = reader.result;
					this.base64Data = arrayBufferToBase64(reader.result);
					console.debug("file readed as bytes: " + arrayBufferToBase64(reader.result));
				});
			});
			reader.readAsArrayBuffer(file);
		}
	}
	function containsAttachment(cd) {
		if (cd.files && cd.files.length > 0) {
			return true;
		}
		if (cd.items) {
			for (var i = 0; i < cd.items.length; ++i) {
				var dataTransferItem = cd.items[i];
				var type = dataTransferItem.type;
				var kind = dataTransferItem.kind;
				if ("file" == kind) {
					return true;
				}
			}
		}
		return false;
	}
	function findImage(types) {
		for (var i = 0; i < types.length; ++i) {
			if (types[i].indexOf('image/') != -1) {
				return true;
			}
		}
		return false;
	}

	function findFileName(cd) {

		if (cd.files && cd.files.length > 0) {
			for (var i = 0; i < cd.files.length; ++i) {
				var file = cd.files[i];
				if (file.name) {
					return file.name;
				}
			}
		}
		if (cd.items) {
			for (var i = 0; i < cd.items.length; ++i) {
				var dataTransferItem = cd.items[i];
				var kind = dataTransferItem.kind;
				if ("file" == kind) {
					var file = dataTransferItem.getAsFile();
					return file.name;
				}
			}
		}
		return null;
	}
	function findTypes(cd) {
		var types = [];

		if (cd.items) {
			for (var i = 0; i < cd.items.length; ++i) {
				var dataTransferItem = cd.items[i];
				var type = dataTransferItem.type;
				types[types.length] = type;
			}
			return types;
		}
		if (cd.files && cd.files.length > 0) {
			for (var i = 0; i < cd.files.length; ++i) {
				var file = cd.files[i];
				types[types.length] = file.type;
			}
		}
		return types;
	}
	function findBinaryData(cd, callback) {
		if (cd.files) {
			for (var i = 0; i < cd.files.length; ++i) {
				var file = cd.files[i];

				var reader = new FileReader();
				reader.onload = (function() {
					callback(file, null, reader.result);
				});
				reader.readAsDataURL(file);
				reader = new FileReader();
				reader.onload = (function() {
					callback(file, reader.result, null);
				});
				reader.readAsArrayBuffer(file);
				return;
			}
		}
		if (cd.items) {
			for (var i = 0; i < cd.items.length; ++i) {
				var dataTransferItem = cd.items[i];
				var kind = dataTransferItem.kind;
				if ("file" == kind) {
					var file = dataTransferItem.getAsFile();
					var reader = new FileReader();
					reader.onload = (function() {
						callback(file, null, reader.result);
					});
					reader.readAsDataURL(file);
					reader = new FileReader();
					reader.onload = (function() {
						callback(file, reader.result, null);
					});
					reader.readAsArrayBuffer(file);
					return;
				}
			}
		}
		console.warn("No file found in clipboard");
	}
	function arrayBufferToBase64(buffer) {
		var binary = '';
		var bytes = new Uint8Array(buffer);
		var len = bytes.byteLength;
		for (var i = 0; i < len; i++) {
			binary += String.fromCharCode(bytes[i]);
		}
		return window.btoa(binary);
	}
}
