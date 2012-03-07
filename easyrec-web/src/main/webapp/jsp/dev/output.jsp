<div class="appendbody">
    <h1>Performance</h1>

    <p>
        This section shows the computation time for
        <a href="${webappPath}/API">REST-API</a>
        calls.
    </p>
    ${outstr}
</div>
<script type="text/javascript">
    function jamonreset()
    {
        confirm("You are about to reset the performance stats. Do you want to continue?", function ()
        {
            $.ajax({url:"${webappPath}/dev/jamonreset",
                    data: ({tenantId : "${tenantId}" , operatorId: "${operatorId}"}),
                    cache: false,
                    success: function() {
                        window.location = "${webappPath}/dev/jamonreport?tenantId=${tenantId}&operatorId=${operatorId}"
                    }
            });
        });
    }
</script>

