<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Demystifying Email </title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin: 0; padding: 0;">
	<table align="center" border="0" cellpadding="0" cellspacing="0" width="600" style="border: 1px solid #cccccc;">
	 <tr>
		<td style="padding: 40px 0 30px 0; left:10px;">
			<div style=" top:2px;left:60px;position:relative; font-family: Tahoma,Arial, sans-serif; font-size: 36px; line-height: 45px;"><img src="mono.png"> CAULIFLOWER</div>
		</td>
	 </tr>
	 
	  <tr>
		<td bgcolor="#bbb" style="padding: 40px 30px 40px 30px;">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					 <tr>
						<td style="font-family: Tahoma,Arial; font-size: 28px; line-height: 20px;padding: 20px 0 30px 0;">
							Dear ,<b>${to}</b>
							<hr>
						</td>
					 </tr>
					 <tr>
						<td style="padding: 20px 0 30px 0; font-family: Tahoma,Arial, sans-serif; font-size: 22px; color:#fff; line-height: 25px;text-transform:uppercase;  ">
							<p>${body}</p>
							
						</td>
					 </tr>
					
				</table>
		</td>
	 </tr>
	 
	 <tr>
			<td bgcolor="#37B87C" style="padding: 30px 30px 30px 30px; text-align: center; color: #153643; font-family: Arial, sans-serif; font-size: 20px;">
				 Regards,<b>${from}</b>.
				</td>
	 </tr>
	</table>	
   
 
</body>
</html>