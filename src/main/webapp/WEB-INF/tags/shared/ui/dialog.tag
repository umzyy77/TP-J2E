<%@ tag pageEncoding="UTF-8" %>


<div id="confirmDialog" class="fixed inset-0 z-50 hidden">

    <div class="fixed inset-0 bg-black/50 backdrop-blur-sm" onclick="closeDialog()"></div>

    <div class="fixed left-1/2 top-1/2 z-50 w-full max-w-md -translate-x-1/2 -translate-y-1/2 p-4">
        <div class="rounded-lg border border-zinc-200 bg-white p-6 shadow-lg">
            <div class="flex items-start gap-4">
                <div id="dialogIcon" class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-red-100">
                    <i data-lucide="triangle-alert" class="h-5 w-5 text-red-600"></i>
                </div>
                <div class="flex-1">
                    <h3 id="dialogTitle" class="text-lg font-semibold text-zinc-900"></h3>
                    <p id="dialogMessage" class="mt-2 text-sm text-zinc-600"></p>
                </div>
            </div>
            <div class="mt-6 flex justify-end gap-3">
                <button onclick="closeDialog()" 
                        class="inline-flex h-10 items-center justify-center rounded-md border border-zinc-200 bg-white px-4 text-sm font-medium text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50">
                    Annuler
                </button>
                <form id="dialogForm" method="post" action="#" class="inline">
                    <input type="hidden" id="dialogFormId" name="id" value="" />
                    <button id="dialogConfirmBtn" type="submit"
                       class="inline-flex h-10 items-center justify-center rounded-md bg-red-600 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-red-700">
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function openDialog(options) {
        const { actionUrl, entityId, title, message, confirmLabel, variant = 'danger' } = options;

        document.getElementById('dialogTitle').textContent = title || 'Confirmation';
        document.getElementById('dialogMessage').textContent = message || 'Êtes-vous sûr ?';
        document.getElementById('dialogConfirmBtn').textContent = confirmLabel || 'Confirmer';
        document.getElementById('dialogForm').action = actionUrl;
        document.getElementById('dialogFormId').value = entityId;

        const icon = document.getElementById('dialogIcon');
        const btn = document.getElementById('dialogConfirmBtn');
        
        if (variant === 'danger') {
            icon.className = 'flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-red-100';
            icon.querySelector('svg').className = 'h-5 w-5 text-red-600';
            btn.className = 'inline-flex h-10 items-center justify-center rounded-md bg-red-600 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-red-700';
        } else {
            icon.className = 'flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-zinc-100';
            icon.querySelector('svg').className = 'h-5 w-5 text-zinc-600';
            btn.className = 'inline-flex h-10 items-center justify-center rounded-md bg-zinc-900 px-4 text-sm font-medium text-white shadow transition-colors hover:bg-zinc-800';
        }
        
        document.getElementById('confirmDialog').classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
    
    function closeDialog() {
        document.getElementById('confirmDialog').classList.add('hidden');
        document.body.style.overflow = '';
    }
    
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') closeDialog();
    });
</script>
