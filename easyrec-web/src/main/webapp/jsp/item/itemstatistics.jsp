<%@ page language="java" session="false" %>

<p>
    Here you can see some statistics for the current item.
</p>

<table width="100%">
    <tr>
        <td width="140px"><strong>first action</strong></td>
        <td>${itemDetails.minActionTime}</td>
    </tr>
    <tr>
        <td><strong>last action</strong></td>
        <td>${itemDetails.maxActionTime}</td>
    </tr>
    <tr>
        <td><strong>total actions</strong></td>
        <td>${itemDetails.actions}</td>
    </tr>
    <tr>
        <td><strong>total users</strong></td>
        <td>${itemDetails.users}</td>
    </tr>
</table>