<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
	<h1>Welcome!</h1>
		<p>There are ${deviceCount} devices registered.</p>
		<form name="form" method="POST" action="sendAll">
            <input type="text" name="message" />
            <input type="submit" value="Send Message" />
        </form>
</body>
</html>