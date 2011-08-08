<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Ajax Upload</title>
		<script type="text/javascript">
			var context_path = "<%=request.getContextPath()%>";
		</script>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/swfobject.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.uploadify.v2.1.4.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/upload.js"></script>
	</head>
	<body>
		<H3>
			Ajax Upload
		</H3>
		<div>
			<form action="save.do" method="post">
				<p>
					Subject:
					<br />
					<input type="text" name="subject" />
				</p>
				<p>
					Content:
					<br />
					<textarea name="content"></textarea>
				</p>
				<p>
					File:
					<br />
					<input type="file" id="upload_file" name="upload_file" />
				</p>
				<p>
					<input type="submit" value="Save" />
				</p>
			</form>
		</div>
	</body>
</html>
