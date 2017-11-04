# RESTful authentication

My way to authenticate users using Spring Security

# How do I make it work?

To make it work without any change, you need to have a MySQL database.<br>
Then you can modify the spring config file (application.yml) to use your own DB.<br>
You can custom the User entity for your own need, the application will create the table.

# Why is it RESTful?

It is RESTful because it is completely stateless. <br>
The server won't use Java's sessions but will instead create a Json Web Token and set a new header in the response.<br>
As it is a custom header, you also need to expose it, otherwise the client will not receive it.<br>
As it is stateless, the client needs to send the header for each request.<br>